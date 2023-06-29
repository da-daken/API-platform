import {PageContainer} from '@ant-design/pro-components';
import React, {useEffect, useState} from 'react';
import {List, message} from "antd";
import {
  listInterfaceInfoByPageUsingGET
} from "@/services/heartApi-backend/interfaceInfoController";
import {Link} from "umi";


const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  const loadData = async (current = 1, pageSize = 10) => {
    setLoading(true);
    try {
      const res = await listInterfaceInfoByPageUsingGET({
        current, pageSize
      });
      setList(res?.data?.records ?? []);
      setTotal(res?.data?.total ?? 0)
    } catch (error: any) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false);
  }

  useEffect(() => {
    loadData();
  }, [])

  return (
    <PageContainer title={"在线接口开放平台"}>
      <List
        className="my-list"
        loading={loading}
        itemLayout="horizontal"
        dataSource={list}
        renderItem={(item) => {
          const apiLink = `/interface_info/${item.id}`;
          return <List.Item
            actions={[<Link key={item.id} to={apiLink}>查看</Link>]}
          >
            <List.Item.Meta
              title={<Link to={apiLink}>{item.name}</Link>}
              description={item.description}
            />
          </List.Item>
        }
        }
        pagination={
          {
            // eslint-disable-next-line @typescript-eslint/no-shadow
            showTotal(total: number) {
              return '总数:' + total
            },
            pageSize: 10,
            total,
            onChange(page, pageSize) {
              loadData(page, pageSize);
            }
          }
        }
      />
    </PageContainer>
  );
};

export default Index;
