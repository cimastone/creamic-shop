// 验证邮箱
export const validateEmail = (email) => {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  return re.test(email)
}

// 验证密码强度
export const validatePassword = (password) => {
  const minLength = 8
  const hasUpperCase = /[A-Z]/.test(password)
  const hasLowerCase = /[a-z]/.test(password)
  const hasNumbers = /\d/.test(password)
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password)
  
  return {
    isValid: password.length >= minLength && hasUpperCase && hasLowerCase && hasNumbers && hasSpecialChar,
    errors: {
      length: password.length < minLength ? 'Password must be at least 8 characters long' : '',
      uppercase: !hasUpperCase ? 'Password must contain at least one uppercase letter' : '',
      lowercase: !hasLowerCase ? 'Password must contain at least one lowercase letter' : '',
      numbers: !hasNumbers ? 'Password must contain at least one number' : '',
      special: !hasSpecialChar ? 'Password must contain at least one special character' : ''
    }
  }
}

// 验证手机号
export const validatePhone = (phone) => {
  const re = /^\+?[\d\s-]{10,}$/
  return re.test(phone)
}

// 验证URL
export const validateUrl = (url) => {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

// 验证必填字段
export const validateRequired = (value) => {
  return value !== null && value !== undefined && value !== ''
} 