#!/bin/bash

# ============================================================
# E-Commerce Platform - Modular Monolith Project Generator
# ============================================================

PROJECT_ROOT="ecommerce-platform"

echo "🚀 Generating E-Commerce Platform Project..."

# Create root directory
mkdir -p $PROJECT_ROOT
cd $PROJECT_ROOT

# ============================================================
# MODULE DIRECTORIES
# ============================================================

# Shared Core
mkdir -p shared-core/src/main/java/com/platform/core/{config,domain,event,exception,exception,repository,service,dto,security,multitenancy,validation,util,cloud/storage,cloud/messaging}
mkdir -p shared-core/src/main/resources
mkdir -p shared-core/src/test/java/com/platform/core

# IAM Module
mkdir -p iam-module/src/main/java/com/platform/iam/{domain/model,domain/repository,domain/service,domain/event,application/service,application/dto,application/mapper,infrastructure/security,infrastructure/persistence,api,integration}
mkdir -p iam-module/src/test/java/com/platform/iam

# Catalog Module
mkdir -p catalog-module/src/main/java/com/platform/catalog/{domain/model,domain/repository,domain/service,domain/event,application/service,application/dto,application/mapper,infrastructure/search,infrastructure/persistence,infrastructure/cache,api,integration}
mkdir -p catalog-module/src/test/java/com/platform/catalog

# Inventory Module
mkdir -p inventory-module/src/main/java/com/platform/inventory/{domain/model,domain/repository,domain/service,domain/event,application/service,application/dto,application/mapper,api,integration}
mkdir -p inventory-module/src/test/java/com/platform/inventory

# Cart Module
mkdir -p cart-module/src/main/java/com/platform/cart/{domain/model,domain/repository,domain/service,application/service,application/dto,application/mapper,infrastructure/cache,api,integration}
mkdir -p cart-module/src/test/java/com/platform/cart

# Order Module
mkdir -p order-module/src/main/java/com/platform/order/{domain/model,domain/repository,domain/service,domain/event,application/service,application/saga,application/dto,application/mapper,api,integration}
mkdir -p order-module/src/test/java/com/platform/order

# Payment Module
mkdir -p payment-module/src/main/java/com/platform/payment/{domain/model,domain/repository,domain/service,domain/event,application/service,application/gateway,application/dto,application/mapper,api,integration}
mkdir -p payment-module/src/test/java/com/platform/payment

# Shipping Module
mkdir -p shipping-module/src/main/java/com/platform/shipping/{domain/model,domain/repository,domain/event,application/service,application/provider,application/dto,api,integration}
mkdir -p shipping-module/src/test/java/com/platform/shipping

# Promotion Module
mkdir -p promotion-module/src/main/java/com/platform/promotion/{domain/model,domain/repository,domain/service,domain/event,application/service,application/dto,api,integration}
mkdir -p promotion-module/src/test/java/com/platform/promotion

# Notification Module
mkdir -p notification-module/src/main/java/com/platform/notification/{domain/model,domain/repository,application/service,application/channel,application/listener,application/dto,infrastructure/email,infrastructure/sms,infrastructure/push,api,integration}
mkdir -p notification-module/src/test/java/com/platform/notification

# Settlement Module
mkdir -p settlement-module/src/main/java/com/platform/settlement/{domain/model,domain/repository,domain/service,application/service,application/dto,api,integration}
mkdir -p settlement-module/src/test/java/com/platform/settlement

# Analytics Module
mkdir -p analytics-module/src/main/java/com/platform/analytics/{domain/model,domain/repository,application/service,application/collector,application/dto,api,integration}
mkdir -p analytics-module/src/test/java/com/platform/analytics

# CMS Module
mkdir -p cms-module/src/main/java/com/platform/cms/{domain/model,domain/repository,application/service,application/dto,api}
mkdir -p cms-module/src/test/java/com/platform/cms

# Support Module
mkdir -p support-module/src/main/java/com/platform/support/{domain/model,domain/repository,application/service,application/dto,api}
mkdir -p support-module/src/test/java/com/platform/support

# Platform App
mkdir -p platform-app/src/main/java/com/platform/config
mkdir -p platform-app/src/main/resources/db/migration
mkdir -p platform-app/src/test/java/com/platform

echo "✅ Directory structure created!"
echo "📝 Now creating source files..."