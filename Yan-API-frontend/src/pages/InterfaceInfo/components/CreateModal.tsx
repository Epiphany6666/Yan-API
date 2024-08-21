import { ProColumns, ProTable } from '@ant-design/pro-components';
import '@umijs/max';
import { Modal } from 'antd';
import React from 'react';

export type FormValueType = {
  target?: string;
  template?: string;
  type?: string;
  time?: string;
  frequency?: string;
} & Partial<API.RuleListItem>;
export type props = {
  columns: ProColumns<API.InterfaceInfo>[];
  // 当用户点击取消按钮时触发
  onCancel: () => void;
  // 当用户提交表单时,将用户输入的数据作为参数传递给后台
  onSubmit: (values: API.InterfaceInfo) => Promise<void>;
  // 模态框是否可见
  visible: boolean;
  // values不用传递
  // values: Partial<API.RuleListItem>;
};
const CreateModal: React.FC<props> = (props) => {
  const {columns, visible, onCancel, onSubmit} = props
  return (
    <Modal visible={visible} onCancel={() => onCancel?.()}>
      <ProTable type="form" columns={columns} onSubmit={async (values) => {
        await onSubmit(values);
      }} />
    </Modal>
  );
};
export default CreateModal;
