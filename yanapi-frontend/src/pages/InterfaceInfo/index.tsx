import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Card, List, message } from 'antd';
import {
  getInterfaceInfoVoByIdUsingGet,
  listInterfaceInfoByPageUsingPost,
} from '@/services/Yan-API-backend/interfaceInfoController';
import { useMatch, useParams } from 'react-router';

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const params = useParams()

  const loadData = async () => {
    if (!params.id) {
      message.error('参数不存在')
    }
    setLoading(true)
    try {
      const res = await getInterfaceInfoVoByIdUsingGet({
        id: Number(params.id)
      })
      setData(res.data)
    } catch (error) {
      message.error('请求失败，' + error.message);
    }
    setLoading(false)
  }
  useEffect(() => {
    loadData()
  }, [])

  return (
    <PageContainer title='查看接口'>
      <Card>

      </Card>
    </PageContainer>
  );
};

export default Index;
