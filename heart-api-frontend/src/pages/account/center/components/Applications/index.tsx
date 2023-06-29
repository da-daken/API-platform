import {
  DownloadOutlined,
  EditOutlined,
  EllipsisOutlined,
  ShareAltOutlined,
} from '@ant-design/icons';
import {Link, useRequest} from 'umi';
import { Avatar, Card, Dropdown, List, Menu, Tooltip } from 'antd';
import React from 'react';
import numeral from 'numeral';
import type { ListItemDataType } from '../../data.d';
import { queryFakeList } from '../../service';
import stylesApplications from './index.less';
import {selfInterfaceDataUsingGET} from "@/services/heartApi-backend/userInterfaceInfoController";


export function formatWan(val: number) {
  const v = val * 1;
  if (!v || Number.isNaN(v)) return '';

  let result: React.ReactNode = val;
  if (val > 10000) {
    result = (
      <span>
        {Math.floor(val / 10000)}
        <span
          style={{
            position: 'relative',
            top: -2,
            fontSize: 14,
            fontStyle: 'normal',
            marginLeft: 2,
          }}
        >
          万
        </span>
      </span>
    );
  }
  return result;
}

const Applications: React.FC = () => {
  // 获取tab列表数据
  const { data: listData } = useRequest(() => {
    return selfInterfaceDataUsingGET();
  });


  const CardInfo: React.FC<{
    activeUser: React.ReactNode;
    newUser: React.ReactNode;
  }> = ({ activeUser, newUser }) => (
    <div className={stylesApplications.cardInfo}>
      <div>
        <p>已调用次数</p>
        <p>{activeUser}</p>
      </div>
      <div>
        <p>剩余调用次数</p>
        <p>{newUser}</p>
      </div>
    </div>
  );
  return (
    <List<API.SelfInterfaceDateVo>
      className={stylesApplications.filterCardList}
      grid={{ gutter: 24, xxl: 3, xl: 2, lg: 2, md: 2, sm: 2, xs: 1 }}
      dataSource={listData || []}
      renderItem={(item) => (
        <List.Item >
          <Card
            hoverable
            bodyStyle={{ paddingBottom: 20 }}
            actions={[
              <Tooltip title="分享" key="share">
                <ShareAltOutlined />
              </Tooltip>,
            ]}
          >
            <Card.Meta  title={item.interfaceName} />
            <div className={stylesApplications.cardItemContent}>
              <CardInfo
                activeUser={item.totalNum}
                newUser={item.leftNum}
              />
            </div>
          </Card>
        </List.Item>
      )}
    />
  );
};

export default Applications;
