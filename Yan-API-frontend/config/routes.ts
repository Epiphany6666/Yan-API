export default [
  {
    path: '/user',
    layout: false,
    routes: [{ name: '登录', path: '/user/login', component: './User/Login' }],
  },
  // { path: '/welcome', name: '欢迎', icon: 'smile', component: './Welcome' },
  {
    path: '/admin',
    name: '管理页',
    icon: 'crown',
    // 权限控制可以去看 and design pro 的官方文档，不用纠结为什么这么写，就是人家设定的规则而已
    access: 'canAdmin',
    routes: [
      { name: '接口管理', icon: 'table', path: '/admin/interface_info', component: './InterfaceInfo' },
    ],
  },

  // { path: '/', redirect: '/welcome' },
  { path: '*', layout: false, component: './404' },
];
