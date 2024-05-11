<template>
    <div style="width: 100vw;height:100vh;overflow: hidden;display:flex">
        <div style="flex:1;background-color: black;">
            <el-image style="width: 100%;height: 100%;" fit="cover"
                src="https://files.codelife.cc/wallhaven/full/xl/wallhaven-xldyvv.jpg?x-oss-process=image/resize,limit_0,m_fill,w_2560,h_1440/quality,Q_92/format,webp" />
        </div>
        <div class="welcome-title">
            <div style="font-size: 30px;font-weight: bold;">欢迎来到我的博客平台</div>
            <div style="margin-top: 10px;">在这里你可以学习如何使用java，如何搭建网站</div>
            <div style="margin-top: 5px;">在这里你可以同性交友，因为都是男的，没有学java的女生</div>
        </div>
        <div class="right-card">
            <RouterView />
        </div>
    </div>
</template>
<script setup>

</script>

<style scoped>
.welcome-title {
    position: absolute;
    bottom: 30px;
    left: 30px;
    color: white;
    text-shadow: 0 0 10px black;
}

.right-card {
    width: 400px;
    z-index: 1;
    background-color: white;
}
</style>