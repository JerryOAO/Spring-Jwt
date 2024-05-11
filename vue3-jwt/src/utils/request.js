import axios from 'axios'

const baseURL = 'http://localhost:8080'

const instance = axios.create({
    baseURL,
    timeout: 5000
})


instance.interceptors.request.use(
    (config) => {
        return config
    },
    (err) => Promise.reject(err)
)

instance.interceptors.response.use(
    (res) => {
        if (res.data.code != 200) {
            console.log(res)
            ElMessage.warning(res.data.message)
            return
        }
        return res.data
    },
    (err) => {
        ElMessage.warning('发生了一些错误，请联系管理员')
        return Promise.reject(err)
    }
)
export default instance
