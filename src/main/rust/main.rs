use core::ptr::write_volatile;

static HELLO: &[u8] = b"Hello World!";

#[no_mangle]
pub extern "C" fn _start(kernel_base: u32) -> ! {
    let vga_buffer = 0xb8000 as *mut u8;
    let mut color: u8 = 1;
    let mut ptr: isize = 0;
    for (_, &byte) in HELLO.clone().iter().enumerate() {
        unsafe {
            write_volatile(vga_buffer.offset(ptr), byte);
            write_volatile(vga_buffer.offset(ptr + 1), color);
        }
        color += 1;
        if color == 255 {
            color = 1
        }
        ptr += 2;
    }
    loop {}
}
