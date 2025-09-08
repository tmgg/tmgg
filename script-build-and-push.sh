#!/bin/bash

# Docker多镜像构建和推送脚本
set -e

# 版本好
TAG="1.1.33"

# 配置变量
REGISTRY="registry.cn-hangzhou.aliyuncs.com/mxvc"
NAMESPACE="mxvc"

# 定义镜像列表：镜像名称 Dockerfile路径
IMAGES=(
    "tmgg-base-maven"
    "tmgg-base-node"
    "tmgg-base-jdk"
)



# 构建和推送所有镜像
for image_name in "${IMAGES[@]}"; do

    # 完整镜像地址
    full_image_name="${REGISTRY}/${NAMESPACE}/${image_name}"
    dockerfile_path=tmgg-base/${image_name}/Dockerfile

    echo "开始构建镜像: ${image_name}:${TAG}"
    echo "Dockerfile: ${dockerfile_path}"

    # 构建镜像
    docker build -t ${image_name}:${TAG} -f ${dockerfile_path}

    # 标记镜像
    docker tag ${image_name}:${TAG} ${full_image_name}:${TAG}

    # 推送镜像
    echo "推送镜像: ${full_image_name}:${TAG}"
    docker push ${full_image_name}:${TAG}

    echo "镜像推送成功: ${full_image_name}:${TAG}"
    echo "----------------------------------------"
done

echo "所有镜像构建和推送完成!"
