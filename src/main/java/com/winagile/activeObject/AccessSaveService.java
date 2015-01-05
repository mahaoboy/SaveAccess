package com.winagile.activeObject;

import java.util.List;

import com.atlassian.activeobjects.tx.Transactional;

@Transactional
public interface AccessSaveService {

	SaveAccess add(Long pageId, String userkey);

	List<SaveAccess> all();

	int getAccessCount(Long pageId, String userkey);

	List<SaveAccess> getAccessByFilter(Long pageId, String userkey);
	
	List<SaveAccess> getAccessByFilterWithDate(Long pageId, String userkey, Long startDate, Long endDate);
	
	List<SaveAccess> getAccessByFilterWithStartDate(Long pageId, String userkey, Long startDate);
	
	List<SaveAccess> getAccessByFilterWithEndDate(Long pageId, String userkey, Long endDate);

	int getCurrentItemsNum();

	List<SaveAccess> filterWithLimit(int start, int end);
	
	List<SaveAccess> filterWithDate(Long startDate, Long endDate);
	
	List<SaveAccess> filterWithStartDate(Long startDate);
	
	List<SaveAccess> filterWithEndDate(Long endDate);
}
