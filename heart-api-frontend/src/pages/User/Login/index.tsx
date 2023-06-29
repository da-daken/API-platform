import Footer from '@/components/Footer';
import {getFakeCaptcha} from '@/services/ant-design-pro/login';
import {
  AlipayCircleOutlined,
  LockOutlined,
  MobileOutlined,
  TaobaoCircleOutlined,
  UserOutlined,
  WeiboCircleOutlined,
} from '@ant-design/icons';
import {
  LoginForm,
  ProFormCaptcha,
  ProFormCheckbox,
  ProFormText,
  LoginFormPage
} from '@ant-design/pro-components';
import {history, useModel} from '@umijs/max';
import {Alert, Divider, message, Space, Tabs} from 'antd';
import React, {useState} from 'react';
import {flushSync} from 'react-dom';
import styles from './index.less';
import {userLoginUsingPOST} from "@/services/heartApi-backend/userController";
import {Link} from "umi";

const LoginMessage: React.FC<{
  content: string;
}> = ({content}) => {
  return (
    <Alert
      style={{
        marginBottom: 24,
      }}
      message={content}
      type="error"
      showIcon
    />
  );
};
const Login: React.FC = () => {
  const [userLoginState, setUserLoginState] = useState<API.LoginResult>({});
  const [type, setType] = useState<string>('account');
  const {initialState, setInitialState} = useModel('@@initialState');

  const fetchUserInfo = async () => {
    const userInfo = await initialState?.fetchUserInfo?.();

    if (userInfo) {
      await setInitialState((s) => ({...s, currentUser: userInfo}));
    }
  };


  const handleSubmit = async (values: API.UserLoginRequest) => {
    try {
      // 登录
      const user = await userLoginUsingPOST({
        ...values
      });
      console.log(user)
      if (user.data) {
        const defaultLoginSuccessMessage = '登录成功！';
        message.success(defaultLoginSuccessMessage);
        await fetchUserInfo();
        /** 此方法会跳转到 redirect 参数所在的位置 */

        if (user.data) {
          const urlParams = new URL(window.location.href).searchParams;

          history.push(urlParams.get('redirect') || '/');

          return;
        }
        setUserLoginState(user.data);

      }else {
        message.error(user.message);
      }
    } catch (error) {
      const defaultLoginFailureMessage = '登录失败，请重试！';
      console.log(error);
      message.error(defaultLoginFailureMessage);
    }
  };
  const {status, type: loginType} = userLoginState;
  console.log(status);
  return (
    <div  className={styles.container}>
      <div className={styles.content}>
        <LoginFormPage
          backgroundImageUrl="http://yuque.heshuoshi.top/html5/77a5cdb66c9644cdbe6db3753037b5f6.jpg"
          logo={<img alt="logo" src="/logo.svg"/>}
          title="Daken API平台"
          subTitle={'华东交通大学'}
          initialValues={{
            autoLogin: true,
          }}

          onFinish={async (values) => {
            await handleSubmit(values as API.UserLoginRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账户密码登录',
              },

            ]}
          />

          {status === 'error' && loginType === 'account' && (
            <LoginMessage content={'错误的用户名和密码'}/>
          )}
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入账号'}
                rules={[
                  {
                    required: true,
                    message: '用户名是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder={'请输入密码'}
                rules={[
                  {
                    required: true,
                    message: '密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '长度不能小于 8',
                  },
                ]}
              />
            </>
          )}

          {status === 'error' && loginType === 'mobile' && <LoginMessage content="验证码错误"/>}

          <div
            style={{
              marginBottom: 24,
            }}
          >
            <Space split={<Divider type="vertical" />}>
            <ProFormCheckbox noStyle name="autoLogin">
              自动登录
            </ProFormCheckbox>
            <Link to="/user/register">新用户注册</Link>

            <a
              style={{
                float: 'right',
              }}
              target="_blank"
              rel="noreferrer"
            >
              忘记密码
            </a>
            </Space>
          </div>
        </LoginFormPage>
      </div>
      <Footer/>
    </div>
  );
};
export default Login;
