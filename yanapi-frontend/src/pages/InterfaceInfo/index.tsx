import React, { useEffect, useState } from 'react';
import { PageContainer } from '@ant-design/pro-components';
import { Button, Card, Descriptions, Form, message, Input, Divider } from 'antd';
import {
  getInterfaceInfoVoByIdUsingGet,
  invokeInterfaceInfoUsingPost,
} from '@/services/Yan-API-backend/interfaceInfoController';
import { useParams } from 'react-router';

const Index: React.FC = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState<API.InterfaceInfo>();
  const params = useParams()
  // 存储结果变量
  const [invokeRes, setInvokeRes] = useState<any>();
  // 调用加载状态变量，默认为false
  const [invokeLoading, setInvokeLoading] = useState(false);

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

  const onFinish = async (values: any) => {
    if (!params.id) {
      message.error('接口不存在');
      return;
    }

    // 在开始调用接口之前，将 invokeLoading 设置为 true，表示正在加载中
    setInvokeLoading(true);
    try {
      const res = await invokeInterfaceInfoUsingPost({
        id: params.id,
        ...values,
      });

      // 将接口调用的结果（res.data）更新到 invokeRes 状态变量中
      setInvokeRes(res.data);
      message.success('请求成功');
    } catch (error: any) {
      message.error('操作失败，' + error.message);
    }

    // 无论成功或失败，最后将 invokeLoading 设置为 false，表示加载完成
    setInvokeLoading(false);
  }

  return (
    <PageContainer title='查看接口'>
      <Card>
        {
          data ? (
            <Descriptions title={data.name} column={1}>
              <Descriptions.Item label="接口状态">{data.status ? '开启' : '关闭'}</Descriptions.Item>
              <Descriptions.Item label="描述">{data.description}</Descriptions.Item>
              <Descriptions.Item label="请求地址">{data.url}</Descriptions.Item>
              <Descriptions.Item label="请求方法">{data.method}</Descriptions.Item>
              <Descriptions.Item label="请求参数">{data.requestParams}</Descriptions.Item>
              <Descriptions.Item label="请求头">{data.requestHeader}</Descriptions.Item>
              <Descriptions.Item label="响应头">{data.responseHeader}</Descriptions.Item>
              <Descriptions.Item label="创建时间">{data.createTime}</Descriptions.Item>
              <Descriptions.Item label="更新时间">{data.updateTime}</Descriptions.Item>
            </Descriptions>
          ) : (
            <>接口不存在</>
          )
        }
      </Card>
      <Divider />
      <Card>
        {/* 创建一个表单,表单名称为"invoke",布局方式为垂直布局,当表单提交时调用onFinish方法 */}
        <Form name="invoke" layout="vertical" onFinish={onFinish}>
          {/* 创建一个表单项,用于输入请求参数,表单项名称为"userRequestParams" */}
          <Form.Item label="请求参数" name="userRequestParams">
            <Input.TextArea placeholder='{"name": "luoyan"}' />
          </Form.Item>
          {/* 创建一个包裹项,设置其宽度占据 16 个栅格列 */}
          <Form.Item wrapperCol={{ span: 16 }}>
            {/* 创建调用按钮*/}
            <Button type="primary" htmlType="submit">
              调用
            </Button>
          </Form.Item>
        </Form>
      </Card>
      <Divider />
      <Card title="返回结果" loading={invokeLoading}>
        {invokeRes}
      </Card>
    </PageContainer>
  );
};

export default Index;
