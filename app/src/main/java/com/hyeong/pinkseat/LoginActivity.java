package com.hyeong.pinkseat;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

//  **Volley http 라이브러리로 네트워크 요청, 수신 하는 방법**
//  ① RequestQueue 생성
//  ② Request Object 생성     (LoginRequest클래스 필요)
//  ③ 생성된 Object를 RequestQueue로 전달
//  ④ 이후 할당된 Response로 Callback

// <로그인 화면>
public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("임산부 인증");

        Button Login_ok = (Button) findViewById(R.id.login_ok);
        Button Join = (Button) findViewById(R.id.join);

        final EditText edit_id = (EditText) findViewById(R.id.login_id);
        final EditText edit_pw = (EditText) findViewById(R.id.login_pw);

        //[OK 버튼] 클릭 이벤트 (로그인)
        Login_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = edit_id.getText().toString();
                final String pw = edit_pw.getText().toString();

                //④ Callback 처리부분 (volley 사용을 위한 ResponseListener 구현 부분)
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    //서버로부터 데이터를 받음
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);              //서버로부터 받는 데이터는 JSON타입의 객체
                            boolean success = jsonResponse.getBoolean("success");  //그중 Key값이 "success"인 것을 가져옴

                            //로그인 성공 시, 로그인된 회원 정보를 담아 로그인 후 화면으로 인텐트(로그인 성공시 success값이 true)
                            if (success) {
                                //서버측에서 정보를 받아옴
                                String idx = jsonResponse.getString("user_idx"); //초록글씨 : 서버측에서 user테이블의 값을 담아 전달한 변수(php의 변수)와 이름을 같게해야 함!
                                String name = jsonResponse.getString("name");
                                String date = jsonResponse.getString("date");
                                String hospital = jsonResponse.getString("hospital");

                                //받은 정보를 담아 로그인 후 화면(Main2Activity)으로 인텐트
                                Intent intent = new Intent(LoginActivity.this, Main2Activity.class);
                                intent.putExtra("user_idx", idx);
                                intent.putExtra("name", name);
                                intent.putExtra("date", date);
                                intent.putExtra("hospital", hospital);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //액티비티 쌓인 것 제거
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);   //액티비티 쌓인 것 제거
                                LoginActivity.this.startActivity(intent);
                                //로그인 실패 시 알림창 띄움(로그인 실패시 success값이 false)
                            } else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                builder.setMessage("로그인 실패")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                //② RequestObject 생성 *이때 서버로부터 데이터를 받을 responseListener를 반드시 넘겨준다.
                LoginRequest loginRequest = new LoginRequest(id, pw, responseListener);

                //① RequestQueue 생성
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);

                //③ 생성된 Object를 RequestQueue로 전달
                queue.add(loginRequest);
            }
        });


        //[Join 버튼] 클릭 이벤트 (가입화면 켜기)
        Join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(LoginActivity.this, JoinActivity.class);
                LoginActivity.this.startActivity(intent3);
            }
        });

    }
}
