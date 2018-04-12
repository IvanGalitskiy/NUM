package com.vallsoft.num.presentation.view;


import com.vallsoft.num.domain.utils.User;

// Клас, который реализует этот интерфейс умеет отображать пользователя
public interface IUserDisplayer  {
    void displayUser(User u, String source);
}
