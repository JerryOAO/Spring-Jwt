import router from '@/router'
import request from '@/utils/request'
import { ElMessage } from 'element-plus'

const tokenItemName = 'access_token'

//存入token
const storeAccessToken = (token,remember,expire) => {
    const authObj = {token:token,expire:expire}
    const str = JSON.stringify(authObj)
    if (remember) {
        localStorage.setItem(tokenItemName,str)
    } else {
        sessionStorage.setItem(tokenItemName,str)
    }
}
//获取token
const takeAccessToken = () => {
    const token = localStorage.getItem(tokenItemName) || sessionStorage.getItem(tokenItemName)
    if(!token) return null
    const authObj = JSON.parse(token)
    if (authObj.expire < Date.now()) {
        clearAccessToken()
        ElMessage.warning('登录已过期，请重新登录')
        return null
    }
    return authObj.token
}
//获取token的header
const accessHeader = () => {
    const token = takeAccessToken()
    console.log(token)
    if (token) {
        return {
            'Authorization':`Bearer ${token}`
        }
    } else {
        return {}
    }
}

//清空token
export const clearAccessToken = () => {
    localStorage.removeItem(tokenItemName)
    sessionStorage.removeItem(tokenItemName)
}
//登录
export const login = (username,password,remember) => {
    return request.post('/api/auth/login',{
        username,password
    },{
        headers:{
            'Content-Type':'application/x-www-form-urlencoded'
        }
    }).then(res => {
        storeAccessToken(res.data.token,remember,res.data.expire)
        ElMessage.success(`登录成功, 欢迎回来 ${res.data.username}`)
        router.push("/home")
    })
}
//退出登录
export const logout = () => {
    return request.get('/api/auth/logout',{
        headers:accessHeader()
    }).then(res => {
        if (res.code != 200) {
            console.log(res)
            ElMessage.warning(res.data.message)
            return
        }else{
            console.log(res)
            clearAccessToken()
            ElMessage.success('退出登录成功')
            router.push("/")
        }
    })
}