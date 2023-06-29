import {outLogin} from '@/services/ant-design-pro/api';
import {LogoutOutlined, SettingOutlined, UserOutlined} from '@ant-design/icons';
import {history, useModel} from '@umijs/max';
import {Avatar, Menu, Spin} from 'antd';
import type {ItemType} from 'antd/es/menu/hooks/useItems';
import {stringify} from 'querystring';
import type {MenuInfo} from 'rc-menu/lib/interface';
import React, {useCallback} from 'react';
import {flushSync} from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import styles from './index.less';
import {userLogoutUsingPOST} from "@/services/heartApi-backend/userController";
import {Link} from "umi";

export type GlobalHeaderRightProps = {
  menu?: boolean;
};

const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({menu}) => {
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await userLogoutUsingPOST();
    /** 此方法会跳转到 redirect 参数所在的位置 */
    window.location.href="/user/login"    // Note: There may be security issues, please note
  };
  const {initialState, setInitialState} = useModel('@@initialState');

  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const {key} = event;
      if (key === 'logout') {
        flushSync(() => {
          setInitialState((s) => ({...s, currentUser: undefined}));
        });
        loginOut();
        history.push('/user/login');
        return;
      }
      if (key === 'center') {
        history.push('/account/center');
        return;
      }
      if (key === 'download') {
        history.push('http://yuque.heshuoshi.top/html5/heartApi-client-sdk-0.0.1.jar');
        return;
      }
      history.push(`/account/${key}`);
    },
    [setInitialState],
  );

  const loading = (
    <span className={`${styles.action} ${styles.account}`}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }
  const { currentUser } = initialState;
  if (!currentUser?.data || !currentUser?.data.userAccount) {
    return loading;
  }

  const menuItems: ItemType[] = [
    ...( [
        {
          key: 'center',
          icon: <UserOutlined/>,
          label: '个人中心',
        },
        {
          key: 'download',
          icon: <SettingOutlined/>,
          label: 'SDK下载',
        },
        {
          type: 'divider' as const,
        },
      ]
      ),
    {
      key: 'logout',
      icon: <LogoutOutlined/>,
      label: '退出登录',
    },
  ];

  const menuHeaderDropdown = (
    <Menu className={styles.menu} selectedKeys={[]} onClick={onMenuClick} items={menuItems}/>
  );

  return (
    <HeaderDropdown overlay={menuHeaderDropdown}>
      <span className={`${styles.action} ${styles.account}`}>
        <Avatar size="small" className={styles.avatar} src={currentUser.data.userAvatar} alt="avatar"/>
        <span className={`${styles.name} anticon`}>{currentUser.data.userAccount}</span>
      </span>
    </HeaderDropdown>
  );
};

export default AvatarDropdown;
