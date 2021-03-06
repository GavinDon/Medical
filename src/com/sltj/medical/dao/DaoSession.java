package com.sltj.medical.dao;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig stepTableDaoConfig;

    private final stepTableDao stepTableDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        stepTableDaoConfig = daoConfigMap.get(stepTableDao.class).clone();
        stepTableDaoConfig.initIdentityScope(type);

        stepTableDao = new stepTableDao(stepTableDaoConfig, this);

        registerDao(stepTable.class, stepTableDao);
    }
    
    public void clear() {
        stepTableDaoConfig.getIdentityScope().clear();
    }

    public stepTableDao getStepTableDao() {
        return stepTableDao;
    }

}
