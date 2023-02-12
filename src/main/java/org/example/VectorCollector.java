package org.example;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class VectorCollector implements Collector<Vector2d, List<Vector2d>, Vector2d> {
	@Override
	public Supplier<List<Vector2d>> supplier() {
		return ArrayList::new;
	}

	@Override
	public BiConsumer<List<Vector2d>, Vector2d> accumulator() {
		return (list, Vector2d) -> list.add(Vector2d);
	}

	@Override
	public BinaryOperator<List<Vector2d>> combiner() {
		return (list1, list2) -> {
			list1.addAll(list2);
			return list1;
		};
	}

	@Override
	public Function<List<Vector2d>, Vector2d> finisher() {
		return (list) -> {
			Vector2d baseVector2d = new Vector2d(0,0);
			for(int i = 0; i<list.size(); i++){
				baseVector2d.add(list.get(i));
			}
			return baseVector2d;
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Set.of(Characteristics.UNORDERED);
	}
}
