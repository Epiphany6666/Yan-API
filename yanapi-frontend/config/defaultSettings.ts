import { ProLayoutProps } from '@ant-design/pro-components';
import {SYSTEM_LOGO} from "@/constants";

/**
 * @name
 */
const Settings: ProLayoutProps & {
  pwa?: boolean;
  logo?: string;
} = {
  navTheme: 'light',
  // 拂晓蓝
  colorPrimary: '#1890ff',
  layout: 'mix',
  contentWidth: 'Fluid',
  fixedHeader: false,
  fixSiderbar: true,
  colorWeak: false,
  title: 'Yan-API开放平台',
  pwa: true,
  logo: 'https://luo-yan.oss-cn-guangzhou.aliyuncs.com/favicon.png?Expires=1724820973&OSSAccessKeyId=TMP.3KkbuebtiNknmHeeMcCTipXkyYmmyADKpwLyHcZBViieLvFjg3QDYuA3NQsGf5froA9KF227rToVKfGzKyeyijZ4e6Lcri&Signature=fpsGTH3BYawMzy%2F00yKD9H%2FE0ho%3D',
  iconfontUrl: '',
  token: {
    // 参见ts声明，demo 见文档，通过token 修改样式
    //https://procomponents.ant.design/components/layout#%E9%80%9A%E8%BF%87-token-%E4%BF%AE%E6%94%B9%E6%A0%B7%E5%BC%8F
  },
};

export default Settings;
