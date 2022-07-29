use core::panic::PanicInfo;

#[panic_handler]
fn panic(_info: &PanicInfo) -> ! {
    unsafe {
        core::arch::asm!("out 0xf4, {0}", in(reg) 10)
    }
    loop {}
}
