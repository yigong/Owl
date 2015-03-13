#include <linux/module.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

MODULE_INFO(vermagic, VERMAGIC_STRING);

__visible struct module __this_module
__attribute__((section(".gnu.linkonce.this_module"))) = {
	.name = KBUILD_MODNAME,
	.init = init_module,
#ifdef CONFIG_MODULE_UNLOAD
	.exit = cleanup_module,
#endif
	.arch = MODULE_ARCH_INIT,
};

static const struct modversion_info ____versions[]
__used
__attribute__((section("__versions"))) = {
	{ 0xca05c877, __VMLINUX_SYMBOL_STR(module_layout) },
	{ 0x8d2e268b, __VMLINUX_SYMBOL_STR(param_ops_ulong) },
	{ 0x7ac3714, __VMLINUX_SYMBOL_STR(pci_unregister_driver) },
	{ 0x268f87ce, __VMLINUX_SYMBOL_STR(class_destroy) },
	{ 0x973dba44, __VMLINUX_SYMBOL_STR(device_destroy) },
	{ 0x5551511f, __VMLINUX_SYMBOL_STR(__pci_register_driver) },
	{ 0xd7bd463f, __VMLINUX_SYMBOL_STR(__class_create) },
	{ 0x29537c9e, __VMLINUX_SYMBOL_STR(alloc_chrdev_region) },
	{ 0x7485e15e, __VMLINUX_SYMBOL_STR(unregister_chrdev_region) },
	{ 0xd5f0f96e, __VMLINUX_SYMBOL_STR(device_create) },
	{ 0x2072ee9b, __VMLINUX_SYMBOL_STR(request_threaded_irq) },
	{ 0x91715312, __VMLINUX_SYMBOL_STR(sprintf) },
	{ 0xfeeb9d8c, __VMLINUX_SYMBOL_STR(cdev_add) },
	{ 0x7c4d2dbc, __VMLINUX_SYMBOL_STR(cdev_init) },
	{ 0xb524cf31, __VMLINUX_SYMBOL_STR(pci_iomap) },
	{ 0xa637f8fc, __VMLINUX_SYMBOL_STR(pci_request_regions) },
	{ 0x1d4aa0c0, __VMLINUX_SYMBOL_STR(pci_enable_device) },
	{ 0x1c8c9783, __VMLINUX_SYMBOL_STR(dev_set_drvdata) },
	{ 0x25563496, __VMLINUX_SYMBOL_STR(kmem_cache_alloc_trace) },
	{ 0x2f7c36b3, __VMLINUX_SYMBOL_STR(kmalloc_caches) },
	{        0, __VMLINUX_SYMBOL_STR(alloc_pages_current) },
	{ 0xc5534d64, __VMLINUX_SYMBOL_STR(ioread16) },
	{ 0xb8e7ce2c, __VMLINUX_SYMBOL_STR(__put_user_8) },
	{ 0x4f8b5ddb, __VMLINUX_SYMBOL_STR(_copy_to_user) },
	{ 0x70778d8f, __VMLINUX_SYMBOL_STR(pci_bus_read_config_byte) },
	{ 0xd4ff98d2, __VMLINUX_SYMBOL_STR(pci_bus_read_config_word) },
	{ 0xb2fd5ceb, __VMLINUX_SYMBOL_STR(__put_user_4) },
	{ 0x78bc02d7, __VMLINUX_SYMBOL_STR(pci_bus_read_config_dword) },
	{ 0x6729d3df, __VMLINUX_SYMBOL_STR(__get_user_4) },
	{ 0xe95d6784, __VMLINUX_SYMBOL_STR(pci_bus_write_config_word) },
	{ 0x71e3cecb, __VMLINUX_SYMBOL_STR(up) },
	{ 0xbdf4c9b3, __VMLINUX_SYMBOL_STR(pci_bus_write_config_dword) },
	{ 0x4f6b400b, __VMLINUX_SYMBOL_STR(_copy_from_user) },
	{ 0xf22449ae, __VMLINUX_SYMBOL_STR(down_interruptible) },
	{ 0x8c183cbe, __VMLINUX_SYMBOL_STR(iowrite16) },
	{ 0xefc29626, __VMLINUX_SYMBOL_STR(remap_pfn_range) },
	{ 0xc715d9e0, __VMLINUX_SYMBOL_STR(boot_cpu_data) },
	{        0, __VMLINUX_SYMBOL_STR(schedule_timeout) },
	{ 0x4f68e5c9, __VMLINUX_SYMBOL_STR(do_gettimeofday) },
	{ 0x1d8912d1, __VMLINUX_SYMBOL_STR(current_task) },
	{ 0x436c2179, __VMLINUX_SYMBOL_STR(iowrite32) },
	{ 0xd5673c5a, __VMLINUX_SYMBOL_STR(pci_bus_write_config_byte) },
	{ 0x6df1a937, __VMLINUX_SYMBOL_STR(try_module_get) },
	{ 0xe484e35f, __VMLINUX_SYMBOL_STR(ioread32) },
	{ 0xeae3dfd6, __VMLINUX_SYMBOL_STR(__const_udelay) },
	{ 0xda3e43d1, __VMLINUX_SYMBOL_STR(_raw_spin_unlock) },
	{ 0xd52bf1ce, __VMLINUX_SYMBOL_STR(_raw_spin_lock) },
	{ 0x5a2743be, __VMLINUX_SYMBOL_STR(module_put) },
	{ 0xf20dabd8, __VMLINUX_SYMBOL_STR(free_irq) },
	{ 0x735bf938, __VMLINUX_SYMBOL_STR(cdev_del) },
	{ 0x8253f570, __VMLINUX_SYMBOL_STR(pci_release_regions) },
	{ 0xbe62c3bb, __VMLINUX_SYMBOL_STR(pci_disable_device) },
	{ 0x5907da33, __VMLINUX_SYMBOL_STR(dev_get_drvdata) },
	{ 0x7a38991d, __VMLINUX_SYMBOL_STR(pci_iounmap) },
	{ 0x27e1a049, __VMLINUX_SYMBOL_STR(printk) },
	{ 0x37a0cba, __VMLINUX_SYMBOL_STR(kfree) },
	{ 0xbf694960, __VMLINUX_SYMBOL_STR(__free_pages) },
	{ 0xbdfb6dbb, __VMLINUX_SYMBOL_STR(__fentry__) },
};

static const char __module_depends[]
__used
__attribute__((section(".modinfo"))) =
"depends=";

MODULE_ALIAS("pci:v00001057d00001801sv*sd*bc*sc*i*");

MODULE_INFO(srcversion, "9F4AFF7870536C09F16C256");
