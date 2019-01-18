package com.pisen.ott.settings.network;

import java.util.Comparator;

import com.pisen.ott.settings.network.AccessPoint.ResourceSort;


/**
 * AccessPoint比较器
 */
public class ComparatorAccessPoint implements Comparator<AccessPoint> {

    //None, Name, Size, Date;
	private ResourceSort sortField;

	public ComparatorAccessPoint(ResourceSort sort) {
		this.sortField = sort;
	}

	@Override
	public int compare(AccessPoint ap1, AccessPoint ap2) {


		switch (sortField) {
		case Signal:
			return compareInt(ap1.level,ap2.level);
		case Name:
			return ap1.SSID.compareToIgnoreCase(ap2.SSID);
		
//		Collections.sort(list, new ComparatorResourceInfo(sort));//排序
		default:
			break;
		}

		return 0;
	}

	private int compareInt(int i1, int i2) {
		return i1 > i2 ? -1 : (i1 == i2 ? 0 : 1);
	}
	
}

