import java.util.ArrayList;
import java.util.List;

public class FilterList<E> {
	public <T> List<T> filterList(List<T> originalList, Filter<T, E> filter,
			E text, E type) {
		List<T> filterList = new ArrayList<T>();
		for (T object : originalList) {
			if (filter.isMatched(object, text, type)) {
				filterList.add(object);
			} else {
				continue;
			}
		}
		return filterList;
	}

}
