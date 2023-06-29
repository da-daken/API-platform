/**
 * @see https://umijs.org/zh-CN/plugins/plugin-access
 * */
export default function access(initialState: { currentUser?: API.CurrentUser | undefined }) {
  const {currentUser} = initialState || {};
  return {
    canUser: currentUser?.data,
    canAdmin: currentUser?.data?.userRole === 'admin',
  };
}
