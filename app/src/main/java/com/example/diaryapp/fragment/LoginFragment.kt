package com.example.diaryapp.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.diaryapp.LoginActivity
import com.example.diaryapp.MainActivity
import com.example.diaryapp.R

class LoginFragment : Fragment() {
    lateinit var loginActivity: LoginActivity
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)
        val txtHello: TextView = view.findViewById(R.id.txt_loginfrag_hello)
        val edtPassword: EditText = view.findViewById(R.id.edt_loginFrag_Password)
        val btLogin: Button = view.findViewById(R.id.bt_loginfrag_login)

        loginActivity = activity as LoginActivity
        var userName = loginActivity.sharedPreferences.getString("username", null)
        var userPassword = loginActivity.sharedPreferences.getString("password", null)
        txtHello.setText(txtHello.text.toString() + userName.toString())

        btLogin.setOnClickListener {
            if(edtPassword.text.toString().equals(userPassword)){
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
            }
            else{
                Toast.makeText(loginActivity.applicationContext, "Sai mật khẩu", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}