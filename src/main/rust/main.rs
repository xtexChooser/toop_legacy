#![no_std]
#![no_main]

extern crate lazy_static;
extern crate bitflags;
extern crate spin;

pub mod arch;
pub mod mem;
pub mod boot;
mod panic_handler;
pub mod vga;

#[no_mangle]
pub extern "C" fn _start(boot_info: &boot::BootInfo) -> ! {
    println!("Hello World{}", "!");
    /*mem::init(boot_info);
    arch::init(boot_info);*/
    loop {}
}
