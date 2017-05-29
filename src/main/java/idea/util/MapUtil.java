/**
 * Summary:
 * Copyright: (c) 2017. All rights reserved.
 * Licence: This software may be copied and used freely for any purpose.
 * Requires: JDK 1.6+
 */
package idea.util;

import java.util.*;

/**
 * 處理 Map 物件相關操作.
 *
 * @author Miles Chen
 *
 */
public class MapUtil {
	/**
	 * 將 Map 型態資料排序，由大到小.
	 *
	 * @param map 欲排序的 Map
	 * @param <K> map 的 key
	 * @param <V> map 的 value
	 * @return 排序過的 Map
	 */
	public static <K, V extends Comparable<? super V>> Map<K, V> sortDescByValue( Map<K, V> map ) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>( map.entrySet() );

		Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 ) {
				return (o2.getValue()).compareTo( o1.getValue() );
			}
		} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
			result.put( entry.getKey(), entry.getValue() );

		return result;
	}

	/**
	 * 移除小於此值的 Map 成員.
	 *
	 * @param map    欲處理的 Map
	 * @param <K> map 的 key
	 * @param <V> map 的 value
	 * @param thresh 移除小於此值的成員
	 */
	public static <K, V extends Comparable<? super V>> void removeLess( Map<K, V> map, V thresh ) {
		Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<K, V> entry = iter.next();
			if (entry.getValue().compareTo(thresh) < 0)
				iter.remove();
		}
	}
}
