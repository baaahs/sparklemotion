	.text
	.file	"helloworld.c"
	.literal_position
	.literal .LCPI0_0, .L.str
	.literal .LCPI0_1, printf
	.literal .LCPI0_2, vTaskDelay
	.global	hello_task
	.p2align	2
	.type	hello_task,@function
hello_task:
	entry	sp, 48
	mov.n	a8, a2
	s32i.n	a2, sp, 8
	s32i.n	a8, sp, 4
	j	LBB0_1
LBB0_1:
	l32r	a10, .LCPI0_0
	l32r	a8, .LCPI0_1
	callx8	a8
	movi.n	a8, 10
	l32r	a9, .LCPI0_2
	s32i.n	a10, sp, 0
	mov.n	a10, a8
	callx8	a9
	j	LBB0_1
Lfunc_end0:
	.size	hello_task, Lfunc_end0-hello_task

	.literal_position
	.literal .LCPI1_0, gpio_pad_select_gpio
	.literal .LCPI1_1, gpio_set_direction
	.literal .LCPI1_2, gpio_set_level
	.literal .LCPI1_3, vTaskDelay
	.global	blinky
	.p2align	2
	.type	blinky,@function
blinky:
	entry	sp, 72
	mov.n	a8, a2
	s32i.n	a2, sp, 36
	movi.n	a2, 13
	l32r	a9, .LCPI1_0
	mov.n	a10, a2
	s32i.n	a8, sp, 32
	s32i.n	a2, sp, 28
	callx8	a9
	movi.n	a11, 2
	l32r	a8, .LCPI1_1
	l32i.n	a10, sp, 28
	callx8	a8
	s32i.n	a10, sp, 24
	j	LBB1_1
LBB1_1:
	movi.n	a8, 13
	movi.n	a11, 0
	l32r	a9, .LCPI1_2
	mov.n	a10, a8
	s32i.n	a8, sp, 20
	s32i.n	a9, sp, 16
	callx8	a9
	movi	a8, 100
	l32r	a9, .LCPI1_3
	s32i.n	a10, sp, 12
	mov.n	a10, a8
	s32i.n	a9, sp, 8
	s32i.n	a8, sp, 4
	callx8	a9
	movi.n	a11, 1
	l32i.n	a10, sp, 20
	l32i.n	a8, sp, 16
	callx8	a8
	l32i.n	a8, sp, 4
	s32i.n	a10, sp, 0
	mov.n	a10, a8
	l32i.n	a9, sp, 8
	callx8	a9
	j	LBB1_1
Lfunc_end1:
	.size	blinky, Lfunc_end1-blinky

	.literal_position
	.literal .LCPI2_0, nvs_flash_init
	.literal .LCPI2_1, hello_task
	.literal .LCPI2_2, .L.str.1
	.literal .LCPI2_3, 2048
	.literal .LCPI2_4, xTaskCreate
	.literal .LCPI2_5, blinky
	.literal .LCPI2_6, .L.str.2
	.global	app_main
	.p2align	2
	.type	app_main,@function
app_main:
	entry	sp, 56
	l32r	a8, .LCPI2_0
	callx8	a8
	l32r	a8, .LCPI2_1
	l32r	a11, .LCPI2_2
	l32r	a12, .LCPI2_3
	movi.n	a9, 5
	movi.n	a13, 0
	l32r	a14, .LCPI2_4
	s32i.n	a10, sp, 20
	mov.n	a10, a8
	s32i.n	a13, sp, 16
	s32i.n	a14, sp, 12
	mov.n	a14, a9
	l32i.n	a15, sp, 16
	l32i.n	a8, sp, 12
	s32i.n	a9, sp, 8
	callx8	a8
	l32r	a8, .LCPI2_5
	l32r	a11, .LCPI2_6
	movi	a12, 512
	s32i.n	a10, sp, 4
	mov.n	a10, a8
	l32i.n	a13, sp, 16
	l32i.n	a14, sp, 8
	l32i.n	a15, sp, 16
	l32i.n	a8, sp, 12
	callx8	a8
	s32i.n	a10, sp, 0
	retw.n
Lfunc_end2:
	.size	app_main, Lfunc_end2-app_main

	.section	.iram1.12,"ax",@progbits
	.literal_position
	.literal .LCPI3_0, 2147483647
	.literal .LCPI3_1, xTaskCreatePinnedToCore
	.p2align	2
	.type	xTaskCreate,@function
xTaskCreate:
	entry	sp, 88
	mov.n	a8, a7
	mov.n	a9, a8
	mov.n	a10, a6
	mov.n	a11, a5
	mov.n	a12, a4
	mov.n	a13, a3
	mov.n	a14, a2
	s32i.n	a2, sp, 52
	s32i.n	a3, sp, 48
	s32i.n	a4, sp, 44
	s32i.n	a5, sp, 40
	s32i.n	a6, sp, 36
	s32i.n	a8, sp, 32
	l32i.n	a8, sp, 52
	l32i.n	a6, sp, 48
	l32i.n	a5, sp, 44
	l32i.n	a4, sp, 40
	l32i.n	a3, sp, 36
	l32i.n	a15, sp, 32
	mov.n	a2, sp
	s32i.n	a8, sp, 28
	l32r	a8, .LCPI3_0
	s32i.n	a8, a2, 0
	l32r	a8, .LCPI3_1
	l32i.n	a2, sp, 28
	s32i.n	a10, sp, 24
	mov.n	a10, a2
	s32i.n	a11, sp, 20
	mov.n	a11, a6
	s32i.n	a12, sp, 16
	mov.n	a12, a5
	s32i.n	a13, sp, 12
	mov.n	a13, a4
	s32i.n	a14, sp, 8
	mov.n	a14, a3
	s32i.n	a9, sp, 4
	callx8	a8
	mov.n	a2, a10
	retw.n
Lfunc_end3:
	.size	xTaskCreate, Lfunc_end3-xTaskCreate

	.type	.L.str,@object
	.section	.rodata.str1.1,"aMS",@progbits,1
.L.str:
	.asciz	"Hello world!\n"
	.size	.L.str, 14

	.type	.L.str.1,@object
.L.str.1:
	.asciz	"hello_task"
	.size	.L.str.1, 11

	.type	.L.str.2,@object
.L.str.2:
	.asciz	"blinky"
	.size	.L.str.2, 7


