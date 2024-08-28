import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import {CSDN_LINK, SYSTEM_LOGO} from "@/constants";

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'CSDN',
          title: 'CSDN',
          href: 'https://blog.csdn.net/qq_39921135',
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: 'https://github.com/Epiphany6666/Yan-API',
          blankTarget: true,
        },
        {
          key: 'LeetCode',
          title: 'LeetCode',
          href: 'https://leetcode.cn/u/shui-bu-xing-u2/',
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
