// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';

/** listTopInterfaceInfoInvoke GET /api/analysis/top/interface/invoke */
export async function listTopInterfaceInfoInvokeUsingGET(options?: { [key: string]: any }) {
  return request<API.BaseResponseListInterfaceVo>('/api/analysis/top/interface/invoke', {
    method: 'GET',
    ...(options || {}),
  });
}
