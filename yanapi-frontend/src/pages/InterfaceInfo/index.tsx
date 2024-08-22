import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { List, message } from 'antd';
import { listInterfaceInfoByPageUsingPost } from '@/services/Yan-API-backend/interfaceInfoController';

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [list, setList] = useState<API.InterfaceInfo[]>([]);
  const [total, setTotal] = useState<number>(0);

  const loadData = async (current = 1, pageSize = 5) => {
    setLoading(true)
    try {
      const res = await listInterfaceInfoByPageUsingPost({
        current, pageSize
      })
      setList(res?.data?.records ?? [])
      setTotal(res?.data?.total ?? [])
    } catch (error) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false)
  }
  useEffect(() => {
    loadData()
  }, [])

  return (
    <PageContainer title='在线接口开放平台'>
      <List
        className='my-list'
        loading={loading}
        itemLayout='horizontal'
        dataSource={list}
        renderItem={(item) => (
          <List.Item
            actions={[<a key='list-loadmore-edit'>查看</a>]}
          >
            <List.Item.Meta
              title={<a href='https://ant.design'>{item.name}</a>}
              description={item.description}
            />
          </List.Item>
        )}
        pagination={
          {
            pageSize: 2,
            total: total,
            showTotal(total:number) {
              return '总数' + total
            },
            onChange: (page, pageSize) => {
              loadData(page, pageSize)
            },
          }
        }
      />
    </PageContainer>
  );
};

export default Index;
