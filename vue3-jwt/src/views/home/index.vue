<template>
    <div>
        <el-button @click="userLogout()">退出登录</el-button>
    </div>
</template>

<script setup>
import { logout } from '@/api'
const userLogout = () => {
    logout(()=>{
        router.push('/')
    })
}
</script>

<style scoped>

</style>