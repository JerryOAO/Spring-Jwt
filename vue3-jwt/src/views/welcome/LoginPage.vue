<template>
    <div style="text-align: center;margin: 0 20px">
        <div style="margin-top: 150px;">
            <div style="font-size: 25px;font-weight: bold;">登录</div>
            <div style="font-size: 14px;color:gray;top: 10px;">请先输入用户名和密码登录</div>
        </div>
        <div style="margin-top: 50px;">
            <el-form ref="formRef" :model="form" :rules="rules">
                <el-form-item prop="username">
                    <el-input v-model="form.username" maxlength="20" type="text" placeholder="用户名/邮箱">
                        <template #prefix>
                            <el-icon>
                                <User />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input v-model="form.password" maxlength="20" type="password" placeholder="密码">
                        <template #prefix>
                            <el-icon>
                                <Lock />
                            </el-icon>
                        </template>
                    </el-input>
                </el-form-item>
                <el-row>
                    <el-col :span="12" style="text-align: left">
                        <el-form-item>
                            <el-checkbox v-model="form.remember">记住我</el-checkbox>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="text-align: right">
                        <el-link>忘记密码？</el-link>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <div style="margin-top: 40px;">
            <el-button style="width: 270px;" type="success" plain @click="userLogin()">立即登录</el-button>
        </div>
        <el-divider>
            <span>没有账号</span>
        </el-divider>
        <div>
            <el-button style="width: 270px;" type="warning" plain>立即注册</el-button>
        </div>
    </div>
</template>

<script setup>
import { User } from '@element-plus/icons-vue'
import { Lock } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'
import { login } from '@/api'
import router from '@/router';

const formRef = ref()
const form = reactive({
    username: 'test',
    password: '123456',
    remember: false
})
const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' }
    ]
}
const userLogin = () => {
    formRef.value.validate((valid) => {
        if (valid) {
            //如果登录成功跳转到首页home
            login(form.username, form.password, form.remember,()=>{
                console.log('登录成功')
            })
        } else {
            console.log('登录失败')
            return false
        }
    })
}
</script>

<style scoped></style>