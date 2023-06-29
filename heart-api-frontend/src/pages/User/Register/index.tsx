import Footer from '@/components/Footer';
import {LockOutlined, UserOutlined,} from '@ant-design/icons';
import {LoginForm, ProFormCheckbox, ProFormText,LoginFormPage} from '@ant-design/pro-components';
import {useModel} from '@umijs/max';
import {Alert, Divider, message, Space, Tabs,Button} from 'antd';
import React, {useState} from 'react';
import styles from './index.less';
import {userLoginUsingPOST, userRegisterUsingPOST} from "@/services/heartApi-backend/userController";
import {history, Link} from "@@/exports";


const Register: React.FC = () => {
  const [type, setType] = useState<string>('account');

  // 表单提交
  const handleSubmit = async (values: API.UserRegisterRequest) => {
    const {userPassword, checkPassword} = values;
    // 校验
    if (userPassword !== checkPassword) {
      message.error('两次输入的密码不一致');
      return;
    }

    try {
      // 注册
      const id = await userRegisterUsingPOST(values);
      if (id.code===40000){
        message.error('账号已被注册！');
      }else {
        const defaultLoginSuccessMessage = '注册成功！';
        message.success(defaultLoginSuccessMessage);

        /** 此方法会跳转到 redirect 参数所在的位置 */
        const urlParams = new URL(window.location.href).searchParams;

        history.push(urlParams.get('redirect') || '/');

        return;
      }
    } catch (error: any) {
      const defaultLoginFailureMessage = '注册失败，请重试！';
      message.error(defaultLoginFailureMessage);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.content}>
        <p></p>
        <LoginFormPage
          backgroundImageUrl="http://yuque.heshuoshi.top/html5/94da8b60d6af442fabc8a7f5e1a54340.jpg"
          submitter={{
            searchConfig: {
              submitText: '注  册'
            }
          }}
          logo={<img alt="logo" src="/logo.svg"/>}
          title="Heart API平台"
          subTitle={'前端真难写'}
          initialValues={{
            autoLogin: true,
          }}
          onFinish={async (values) => {
            await handleSubmit(values as API.UserRegisterRequest);
          }}
        >
          <Tabs
            activeKey={type}
            onChange={setType}
            centered
            items={[
              {
                key: 'account',
                label: '账户密码注册',
              },

            ]}
          />
          {type === 'account' && (
            <>
              <ProFormText
                name="userAccount"
                fieldProps={{
                  size: 'large',
                  prefix: <UserOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请输入账号"
                rules={[
                  {
                    required: true,
                    message: '账号是必填项！',
                  },
                ]}
              />
              <ProFormText.Password
                name="userPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请输入密码"
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
              <ProFormText.Password
                name="checkPassword"
                fieldProps={{
                  size: 'large',
                  prefix: <LockOutlined className={styles.prefixIcon}/>,
                }}
                placeholder="请再次输入密码"
                rules={[
                  {
                    required: true,
                    message: '确认密码是必填项！',
                  },
                  {
                    min: 8,
                    type: 'string',
                    message: '长度不能小于 8',
                  },
                ]}
              />
              <Button block={true} size={"large"} href={"/user/login"}>返回登录</Button>
              <p></p>
            </>
          )}
        </LoginFormPage>
      </div>
      <Footer/>
    </div>
  );
};
export default Register;
