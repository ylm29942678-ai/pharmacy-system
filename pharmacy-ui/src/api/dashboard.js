import request from '@/utils/request'

export function getStatistics() {
  return request({
    url: '/dashboard/statistics',
    method: 'get'
  })
}

export function getRecentOrders() {
  return request({
    url: '/dashboard/recent-orders',
    method: 'get'
  })
}

export function getRecentLogs() {
  return request({
    url: '/dashboard/recent-logs',
    method: 'get'
  })
}
