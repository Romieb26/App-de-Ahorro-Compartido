package com.esba.ahorroscompartidos.Presentation.login

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }
}