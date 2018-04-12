package com.vallsoft.num.domain;

import com.vallsoft.num.domain.utils.User;

/**
 * Created by ivang on 22.11.2017.
 */
// Клас, который реализует этот интерфейс умеет получать пользователя от Базы данных
public interface IUserRetriever {
    void onUserRecieved(User u, String source);
}
