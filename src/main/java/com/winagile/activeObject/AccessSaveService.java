package com.winagile.activeObject;

import java.util.List;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.sal.api.user.UserKey;

@Transactional
public interface AccessSaveService {
	SaveAccess add(Long pageId, UserKey username);

	List<SaveAccess> all();
}
