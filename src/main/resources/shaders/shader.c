#define pi 3.14159265358979323846
#define TYPE_AMOUNT $TYPE_AMOUNT$
#define PARTICLE_AMOUNT_SQUARED $PARTICLE_AMOUNT_SQUARED$
#define PARTICLE_AMOUNT $PARTICLE_AMOUNT$
#define POW 100
#define VARIANCE  0.113056
#define PRECALC_VARIANCE -17.6903481461
#define DIVISION_AMOUNT $DIVISION_AMOUNT$


__kernel void vec2_distance(__constant float *vectors, __constant float *minDistances,
                            __constant float *maxDistances, __constant float *strengths,
                            __constant uchar *types, __global float *resultX, __global float *resultY, float repellingForce) {
    int i = get_global_id(0);
    int grouping = get_global_id(1);
    uchar typeI = types[i];
    int iv = 2 * i;
    float2 vector_i = (float2)(vectors[iv], vectors[iv + 1]);
    float2 ourVect = (float2)(0, 0);
    int row = typeI * TYPE_AMOUNT;

    float groupSize = (1.0f * PARTICLE_AMOUNT) / DIVISION_AMOUNT;
    float start = grouping * groupSize;
    float end = start + groupSize;
    float internalBusiness = 0.0f;
    int endCeil = (int)(ceil(end));
    for(int j = (int)floor(start); j<endCeil; j++){
        if(j == i) continue;
        int jv = j * 2;

        float2 vec =  (float2)(vectors[jv], vectors[jv + 1]) - vector_i;
        float2 dot = vec * vec;
        float distanceSquared = dot.x + dot.y;

        int typeIndex = row + types[j];

        float maxDistance = maxDistances[typeIndex];
        if(distanceSquared >= maxDistance * maxDistance)
            continue;

        float minDistance = minDistances[typeIndex];
        float2 normalizedVector = fast_normalize(vec);
        float diff = distanceSquared -  minDistance * minDistance;
        if(diff < 0) {
            ourVect += (normalizedVector * (float)(min((float)(((1.0-diff)) / diff), 15.0f)*repellingForce));
            continue;
        }
        else{
            float distance = vec.x * normalizedVector.x + vec.y * normalizedVector.y;
            float strength = strengths[typeIndex];
            float percentage = (distance - minDistance) / (maxDistance - minDistance);
            float clamped = smoothstep(minDistance, maxDistance, distance);
            float value = percentage > 0.5 ? (-2 * (clamped - 1)) : (2 * clamped);
            value *= strength;
            ourVect += (normalizedVector) * value;
            continue;
        }
      }
      float maximumForceLength = 50.0f;
      float maximumForcePartition = maximumForceLength / groupSize;
      barrier(CLK_GLOBAL_MEM_FENCE);
      if(grouping == 0) {
        resultX[i] = ourVect.x;
        resultY[i] = ourVect.y;
      }
      for(private int threadClosure = 1; threadClosure < DIVISION_AMOUNT; threadClosure++) {
        if(threadClosure == grouping) {
            resultX[i] += ourVect.x;
            resultY[i] += ourVect.y;
        }
      }
      barrier(CLK_GLOBAL_MEM_FENCE);
      float2 finalResult = (float2)(resultX[i], resultY[i]);
      float finalLength = length(finalResult);
      if(finalLength > maximumForceLength){
        finalResult = fast_normalize(finalResult) * maximumForceLength;
        resultX[i] = finalResult.x;
        resultY[i] = finalResult.y;
      }
  }
