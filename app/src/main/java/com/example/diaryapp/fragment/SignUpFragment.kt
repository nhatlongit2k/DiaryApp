package com.example.diaryapp.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.diaryapp.LoginActivity
import com.example.diaryapp.R

class SignUpFragment : Fragment() {

    lateinit var loginActivity: LoginActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val btSignUp: Button = view.findViewById(R.id.bt_signupfrag)
        val edtName: EditText = view.findViewById(R.id.edt_signup_name)
        val edtPassword: EditText = view.findViewById(R.id.edt_signup_password)

        loginActivity = activity as LoginActivity
        var userName = loginActivity.sharedPreferences.getString("username", null)
        if(userName != null){
            findNavController().navigate(R.id.loginFragment)
        }
        btSignUp.setOnClickListener {
            if(edtName.text.toString().trim().equals("") || edtPassword.text.toString().trim().equals("")){
                Toast.makeText(loginActivity.applicationContext, "Tên đăng nhập hoặc mật khẩu không được để trống", Toast.LENGTH_SHORT).show()
            }else{
                val editor: SharedPreferences.Editor =loginActivity.sharedPreferences.edit()
                editor.putString("username", edtName.text.toString())
                editor.putString("password", edtPassword.text.toString())
                editor.commit()
                findNavController().navigate(R.id.loginFragment)
            }
        }
        return view
    }
}