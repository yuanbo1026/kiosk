package de.nexxoo.kiosk_app;

import de.nexxoo.kiosk_app.entity.BaseEntity;

import java.util.List;

/**
 * Created by b.yuan on 03.08.2015.
 */
public interface ISearchCallback {
	public void onSearchDone(List<List<BaseEntity>> entityList);
}
