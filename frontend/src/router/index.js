import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/user'
import HomeView from '../views/HomeView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import ProductListView from '../views/ProductListView.vue'
import ProductDetailView from '../views/ProductDetailView.vue'
import CartView from '../views/CartView.vue'
import CheckoutView from '../views/CheckoutView.vue'
import OrderList from '../views/orders/OrderList.vue'
import OrderDetail from '../views/orders/OrderDetail.vue'
import OrderDetailView from '../views/orders/OrderDetailView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/products',
      name: 'product-list',
      component: ProductListView
    },
    {
      path: '/products/:id',
      name: 'product-detail',
      component: ProductDetailView
    },
    {
      path: '/about',
      name: 'about',
      // 路由级代码分割 - 这会为该路由生成一个单独的 chunk (About.[hash].js)
      // 当路由被访问时会被懒加载
      component: () => import('../views/AboutView.vue')
    },
    {
      path: '/cart',
      name: 'cart',
      component: CartView,
      meta: { requiresAuth: true }
    },
    {
      path: '/checkout',
      name: 'checkout',
      component: CheckoutView,
      meta: { requiresAuth: true }
    },
    {
      path: '/login',
      name: 'login',
      component: LoginView
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView
    },
    {
      path: '/orders',
      name: 'orders',
      component: OrderList,
      meta: { requiresAuth: true }
    },
    {
      path: '/orders/:id',
      name: 'orderDetail',
      component: OrderDetail,
      meta: { requiresAuth: true }
    },
    {
      path: '/order-detail/:id',
      name: 'orderDetailView',
      component: OrderDetailView,
      meta: { requiresAuth: true }
    }
  ]
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  console.log('路由守卫: 正在访问', to.path);
  const userStore = useUserStore()
  
  // 检查用户是否已登录
  const isLoggedIn = userStore.checkLoginStatus()
  console.log('路由守卫: 用户登录状态', isLoggedIn);
  
  // 如果路由需要认证
  if (to.matched.some(record => record.meta.requiresAuth)) {
    console.log('路由守卫: 该路由需要认证');
    if (!isLoggedIn) {
      console.log('路由守卫: 用户未登录，重定向到登录页');
      // 未登录，重定向到登录页
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      })
    } else {
      console.log('路由守卫: 用户已登录，允许访问');
      // 已登录，允许访问
      next()
    }
  } else {
    console.log('路由守卫: 该路由不需要认证，直接访问');
    // 不需要认证的路由，直接访问
    next()
  }
})

export default router 