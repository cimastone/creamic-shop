/**
 * 格式化日期
 * @param {string|Date} date 日期对象或日期字符串
 * @param {string} format 格式化模式，例如 YYYY-MM-DD HH:mm:ss
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD') {
  if (!date) return '';
  
  const d = typeof date === 'string' ? new Date(date) : date;
  
  if (isNaN(d.getTime())) {
    return '';
  }
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  const seconds = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds);
}

/**
 * 获取相对时间描述，例如：刚刚、5分钟前、1小时前等
 * @param {string|Date} date 日期字符串或Date对象
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(date) {
  if (!date) return ''
  
  const d = typeof date === 'string' ? new Date(date) : date
  
  if (isNaN(d.getTime())) return ''
  
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  
  // 转换为秒
  const seconds = Math.floor(diff / 1000)
  
  if (seconds < 60) {
    return '刚刚'
  }
  
  // 转换为分钟
  const minutes = Math.floor(seconds / 60)
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  }
  
  // 转换为小时
  const hours = Math.floor(minutes / 60)
  
  if (hours < 24) {
    return `${hours}小时前`
  }
  
  // 转换为天
  const days = Math.floor(hours / 24)
  
  if (days < 30) {
    return `${days}天前`
  }
  
  // 转换为月
  const months = Math.floor(days / 30)
  
  if (months < 12) {
    return `${months}个月前`
  }
  
  // 转换为年
  const years = Math.floor(months / 12)
  
  return `${years}年前`
} 