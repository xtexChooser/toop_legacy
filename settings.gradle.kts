rootProject.name = "toop"
include("boot:multiboot")
include("boot:loader")
findProject(":boot:loader")?.name = "loader"
