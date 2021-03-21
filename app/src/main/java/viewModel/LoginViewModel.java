package viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import data.repositories.LoginRepository;

public class LoginViewModel extends ViewModel {
    private MutableLiveData<Boolean> authentication;
    private LoginRepository repository;
    public void init(Context activity){
        if(repository==null)
        {
            repository = LoginRepository.getInstance();;
        }

        authentication = repository.getIsAuthenticated(activity);
    }

    public LiveData<Boolean> getAuthentication() {
        return authentication;
    }

}
