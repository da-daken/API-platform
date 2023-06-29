import {PlusOutlined, HomeOutlined, ContactsOutlined,ClusterOutlined} from '@ant-design/icons';
import { Avatar, Card, Col, Divider, Input, Row, Tag, Typography, Collapse } from 'antd';
import React, { useState, useRef } from 'react';
import { GridContent } from '@ant-design/pro-layout';
import { Link, useRequest } from 'umi';
import type { RouteChildrenProps } from 'react-router';
import Projects from './components/Projects';
import Articles from './components/Articles';
import Applications from './components/Applications';
import type { CurrentUser, TagType, tabKeyType } from './data.d';
import { queryCurrent } from './service';
import styles from './Center.less';
import {generateAkSkUsingGET, getLoginUserUsingGET} from "@/services/heartApi-backend/userController";
import {LockFilled} from "@ant-design/icons";
import moment from "moment/moment";
import notice from "@/pages/account/center/ggbond";


const { Paragraph } = Typography;

const { Panel } = Collapse;

const operationTabList = [

  {
    key: 'applications',
    tab: (
      <span>
        接口 <span style={{ fontSize: 14 }}></span>
      </span>
    ),
  },

];

const TagList: React.FC<{ tags: CurrentUser['tags'] }> = ({ tags }) => {
  const ref = useRef<Input | null>(null);
  const [newTags, setNewTags] = useState<TagType[]>([]);
  const [inputVisible, setInputVisible] = useState<boolean>(false);
  const [inputValue, setInputValue] = useState<string>('');

  const showInput = () => {
    setInputVisible(true);
    if (ref.current) {
      // eslint-disable-next-line no-unused-expressions
      ref.current?.focus();
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(e.target.value);
  };

  const handleInputConfirm = () => {
    let tempsTags = [...newTags];
    if (inputValue && tempsTags.filter((tag) => tag.label === inputValue).length === 0) {
      tempsTags = [...tempsTags, { key: `new-${tempsTags.length}`, label: inputValue }];
    }
    setNewTags(tempsTags);
    setInputVisible(false);
    setInputValue('');
  };

  return (
    <div className={styles.tags}>
      <div className={styles.tagsTitle}>标签</div>
      {(tags || []).concat(newTags).map((item) => (
        <Tag key={item.key}>{item.label}</Tag>
      ))}
      {inputVisible && (
        <Input
          ref={ref}
          type="text"
          size="small"
          style={{ width: 78 }}
          value={inputValue}
          onChange={handleInputChange}
          onBlur={handleInputConfirm}
          onPressEnter={handleInputConfirm}
        />
      )}
      {!inputVisible && (
        <Tag onClick={showInput} style={{ borderStyle: 'dashed' }}>
          <PlusOutlined />
        </Tag>
      )}
    </div>
  );
};

const Center: React.FC<RouteChildrenProps> = () => {
  const [tabKey, setTabKey] = useState<tabKeyType>('applications');

  //  获取用户信息
  const { data: currentUser, loading } = useRequest(() => {
    return getLoginUserUsingGET();
  });

  const tag=[
    {
      key: '0',
      label: '阳光开朗的',
    },
    {
      key: '1',
      label: '英俊潇洒的',
    },
    {
      key: '2',
      label: '热爱Java',
    },
    {
      key: '3',
      label: '喜欢二字游戏',
    },
  ]





  //  渲染用户信息
  const renderUserInfo = ({accessKey,secretKey, createTime }: Partial<CurrentUser>) => {
    return (
      <div className={styles.detail}>
        <p>
          <ContactsOutlined
            style={{
              marginRight: 8,
            }}
          />
          {"java大神"}
        </p>
        <p>
          <ClusterOutlined
            style={{
              marginRight: 8,
            }}
          />
          {"华东交通大学"}
        </p>
        <p>
          <HomeOutlined
            style={{
              marginRight: 8,
            }}
          />
          {"注册时间:"}<b>&nbsp;&nbsp;&nbsp;{moment(createTime).format('YYYY-MM-DD HH:mm:ss')}</b>
        </p>
        <Divider></Divider>
        <Collapse size="small">
          <Panel header="accessKey:" key="1">
            <p><Paragraph copyable>{accessKey}</Paragraph></p>
          </Panel>
          <Panel header="secretKey:" key="1">
            <p><Paragraph copyable>{secretKey}</Paragraph></p>
          </Panel>
        </Collapse>
      </div>
    );
  };

  // 渲染tab切换
  const renderChildrenByTabKey = (tabValue: tabKeyType) => {

    if (tabValue === 'applications') {
      return <Applications />;
    }

    return null;
  };

  // 更换ak/sk
  const generateAkSk = async (): Promise<void> => {
    try {
      await generateAkSkUsingGET();
      window.location.reload();
    } catch (error) {
      // 处理错误
      console.log(error);
    }
  }

  return (
    <GridContent>
      <Row gutter={24}>
        <Col lg={7} md={24}>
          <Card bordered={false} style={{ marginBottom: 24 }} loading={loading}>
            {!loading && currentUser && (
              <div>
                <div className={styles.avatarHolder}>
                  <img alt="" src={currentUser.userAvatar} />
                  <div className={styles.name}>{currentUser.userName}</div>
                  <div>{currentUser?.signature}</div>
                </div>
                {renderUserInfo(currentUser)}
                <Divider dashed />
                <TagList tags={tag || []} />
                <Divider style={{ marginTop: 16 }} dashed />
                <div className={styles.team}>
                  <div className={styles.teamTitle}>更换</div>
                  <Row gutter={36}>
                    {notice &&
                      notice.map((item) => (
                        <Col key={item.id} lg={24} xl={12}>
                          <Link to={item.href}>
                            <Avatar size="small" src={item.logo} />
                            {item.member}
                          </Link>
                        </Col>
                      ))}
                  </Row>
                </div>
              </div>
            )}
            <div>
              <button onClick={() => {
                generateAkSk().then()
              }}>更换ak/sk</button>
            </div>
          </Card>
        </Col>
        <Col lg={17} md={24}>
          <Card
            className={styles.tabsCard}
            bordered={false}
            tabList={operationTabList}
            activeTabKey={tabKey}
            onTabChange={(_tabKey: string) => {
              setTabKey(_tabKey as tabKeyType);
            }}
          >
            {renderChildrenByTabKey(tabKey)}
          </Card>
        </Col>
      </Row>
    </GridContent>
  );
};
export default Center;
