package com.winagile.activeObject;

import java.util.List;

import com.atlassian.activeobjects.tx.Transactional;

@Transactional
public interface AccessSaveService {

	SaveAccess add(Long pageId, String userkey);

	List<SaveAccess> all();

	int getAccessCount(Long pageId, String userkey);

	List<SaveAccess> getAccessByFilter(Long pageId, String userkey);

	int getCurrentItemsNum();

	List<SaveAccess> filterWithLimit(int start, int end);
}
