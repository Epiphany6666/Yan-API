/**
 * @see https://umijs.org/docs/max/access#access
 * */
export default function access(initialState: InitialState | undefined) {
  const { loginUser } = initialState ?? {};
  return {
    // 根据当前用户来判断它是否有用户权限、是否有管理员权限
    canUser: loginUser,
    canAdmin: loginUser?.userRole === 'admin',
  };
}
