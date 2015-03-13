#include <linux/module.h>
#include <linux/vermagic.h>
#include <linux/compiler.h>

MODULE_INFO(vermagic, VERMAGIC_STRING);

struct module __this_module
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
	{ 0x8e49f4de, "module_layout" },
	{ 0x6fab991, "alloc_pages_current" },
	{ 0x52e1ba21, "cdev_del" },
	{ 0x8bf7d1b0, "per_cpu__current_task" },
	{ 0xecf43c07, "kmalloc_caches" },
	{ 0x60449d42, "pci_bus_read_config_byte" },
	{ 0x6ed5c961, "cdev_init" },
	{ 0xb279da12, "pv_lock_ops" },
	{ 0x3a934e48, "dev_set_drvdata" },
	{ 0xbf8ce7a2, "boot_cpu_data" },
	{ 0x2c599cfc, "pci_disable_device" },
	{ 0x799c50a, "param_set_ulong" },
	{ 0x973873ab, "_spin_lock" },
	{ 0x105e2727, "__tracepoint_kmalloc" },
	{ 0x45d11c43, "down_interruptible" },
	{ 0x691b8d3d, "device_destroy" },
	{ 0x6729d3df, "__get_user_4" },
	{ 0xeae3dfd6, "__const_udelay" },
	{ 0xf843b6e3, "pci_release_regions" },
	{ 0x7485e15e, "unregister_chrdev_region" },
	{ 0x59bfd1b2, "pci_bus_write_config_word" },
	{ 0x3c2c5af5, "sprintf" },
	{ 0x9629486a, "per_cpu__cpu_number" },
	{ 0xb8e7ce2c, "__put_user_8" },
	{ 0xf0c57eb, "pci_iounmap" },
	{ 0xea147363, "printk" },
	{ 0xacdeb154, "__tracepoint_module_get" },
	{ 0xa1c76e0a, "_cond_resched" },
	{ 0xbe499d81, "copy_to_user" },
	{ 0xb4390f9a, "mcount" },
	{ 0x74be5608, "pci_bus_write_config_dword" },
	{ 0x44abb265, "device_create" },
	{ 0xfda85a7d, "request_threaded_irq" },
	{ 0x7432f326, "cdev_add" },
	{ 0x9715fa2c, "module_put" },
	{ 0x596903e5, "kmem_cache_alloc" },
	{ 0x2804ea8f, "__free_pages" },
	{ 0xb2fd5ceb, "__put_user_4" },
	{ 0x4c6223ea, "pci_bus_read_config_word" },
	{ 0xebda04e6, "pci_bus_read_config_dword" },
	{ 0xd62c833f, "schedule_timeout" },
	{ 0xa4df7f60, "pci_unregister_driver" },
	{ 0x91766c09, "param_get_ulong" },
	{ 0xc5844fb8, "__per_cpu_offset" },
	{ 0x1d2e87c6, "do_gettimeofday" },
	{ 0x9c46574a, "pci_bus_write_config_byte" },
	{ 0x8c183cbe, "iowrite16" },
	{ 0x37a0cba, "kfree" },
	{ 0x94ef5b9d, "remap_pfn_range" },
	{ 0xc73d3483, "pci_request_regions" },
	{ 0x3f1899f1, "up" },
	{ 0x854c5636, "__pci_register_driver" },
	{ 0xf6c5f396, "class_destroy" },
	{ 0xc5534d64, "ioread16" },
	{ 0x5a872763, "pci_iomap" },
	{ 0x436c2179, "iowrite32" },
	{ 0x86d64aa8, "pci_enable_device" },
	{ 0x733e4a90, "__class_create" },
	{ 0x945bc6a7, "copy_from_user" },
	{ 0xd5296b60, "dev_get_drvdata" },
	{ 0x29537c9e, "alloc_chrdev_region" },
	{ 0xe484e35f, "ioread32" },
	{ 0xf20dabd8, "free_irq" },
};

static const char __module_depends[]
__used
__attribute__((section(".modinfo"))) =
"depends=";

MODULE_ALIAS("pci:v00001057d00001801sv*sd*bc*sc*i*");

MODULE_INFO(srcversion, "E12B1B3396DEE38963E6F5A");
