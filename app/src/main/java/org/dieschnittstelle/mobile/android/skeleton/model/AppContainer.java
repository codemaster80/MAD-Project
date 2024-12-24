package org.dieschnittstelle.mobile.android.skeleton.model;

import retrofit2.Retrofit;

public class AppContainer {
    // Since you want to expose userRepository out of the container, you need to satisfy
    // its dependencies as you did before
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://example.com")
            .build();
            //.create(LoginService.class);

    private UserRemoteDataSource remoteDataSource = new UserRemoteDataSource(retrofit);
    private UserLocalDataSource localDataSource = new UserLocalDataSource();

    // userRepository is not private; it'll be exposed
    public UserRepository userRepository = new UserRepository(localDataSource, remoteDataSource);
}
