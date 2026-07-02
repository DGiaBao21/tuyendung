package com.polyjobs;

import com.polyjobs.entity.Category;
import com.polyjobs.entity.Company;
import com.polyjobs.entity.Job;
import com.polyjobs.entity.User;
import com.polyjobs.repository.CategoryRepository;
import com.polyjobs.repository.CompanyRepository;
import com.polyjobs.repository.JobRepository;
import com.polyjobs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ thêm dữ liệu mẫu nếu CSDL chưa có việc làm nào
        if (jobRepository.count() == 0) {
            System.out.println(">>> Đang khởi tạo dữ liệu mẫu cho khu vực Miền Tây Nam Bộ...");

            // 1. Tạo Tài khoản Nhà tuyển dụng mẫu
            User employer1 = new User();
            employer1.setUsername("ntd_cantho");
            employer1.setPassword("123");
            employer1.setFullname("Trần Văn Tuyển (NTD)");
            employer1.setEmail("tuyen@cantho.vn");
            employer1.setPhone("0909123456");
            employer1.setRole(true); // Nhà tuyển dụng
            userRepository.save(employer1);

            User employer2 = new User();
            employer2.setUsername("ntd_angiang");
            employer2.setPassword("123");
            employer2.setFullname("Lê Thị Giang (NTD)");
            employer2.setEmail("giang@angiang.vn");
            employer2.setPhone("0918765432");
            employer2.setRole(true);
            userRepository.save(employer2);

            // 2. Tạo Ngành nghề
            Category catIT = new Category();
            catIT.setName("Công nghệ thông tin");
            categoryRepository.save(catIT);

            Category catSeafood = new Category();
            catSeafood.setName("Chế biến Thủy sản");
            categoryRepository.save(catSeafood);

            Category catBiz = new Category();
            catBiz.setName("Kinh doanh & Bán hàng");
            categoryRepository.save(catBiz);

            Category catAgri = new Category();
            catAgri.setName("Nông nghiệp Kỹ thuật cao");
            categoryRepository.save(catAgri);

            // 3. Tạo Công ty tại Miền Tây
            Company comp1 = new Company();
            comp1.setCompanyName("FPT Software Cần Thơ");
            comp1.setAddress("Tòa nhà FPT, Q. Ninh Kiều, TP. Cần Thơ");
            comp1.setWebsite("https://fptsoftware.com");
            comp1.setDescription("Đơn vị phần mềm lớn nhất miền Tây Nam Bộ với môi trường làm việc chuẩn quốc tế.");
            comp1.setEmployer(employer1);
            companyRepository.save(comp1);

            Company comp2 = new Company();
            comp2.setCompanyName("Tập đoàn Thủy sản Nam Việt");
            comp2.setAddress("KCN Mỹ Thới, TP. Long Xuyên, An Giang");
            comp2.setWebsite("https://navico.com");
            comp2.setDescription("Chuyên chế biến và xuất khẩu cá tra, cá basa hàng đầu khu vực ĐBSCL.");
            comp2.setEmployer(employer2);
            companyRepository.save(comp2);

            Company comp3 = new Company();
            comp3.setCompanyName("Công ty CP Giống Cây Trồng Miền Tây");
            comp3.setAddress("TP. Cao Lãnh, Đồng Tháp");
            comp3.setWebsite("https://giongcaymientay.vn");
            comp3.setDescription("Nghiên cứu và phát triển các giống lúa, trái cây đặc sản vùng Đồng bằng sông Cửu Long.");
            comp3.setEmployer(employer1);
            companyRepository.save(comp3);

            // 4. Tạo Việc làm
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, 1);
            Date deadline = cal.getTime();

            Job job1 = new Job();
            job1.setTitle("Lập trình viên Java / Spring Boot (Middle/Senior)");
            job1.setSalary("18 - 25 Triệu");
            job1.setLocation("Cần Thơ");
            job1.setExperience("1 - 3 năm");
            job1.setWorkingType("Toàn thời gian");
            job1.setQuantity(5);
            job1.setDescription("Phát triển các hệ thống quản lý doanh nghiệp lớn bằng Spring Boot và Angular/React. Làm việc tại Cần Thơ.");
            job1.setBenefit("Bảo hiểm FPT Care, thưởng 13-14 tháng lương, du lịch thường niên.");
            job1.setDeadline(deadline);
            job1.setCompany(comp1);
            jobRepository.save(job1);

            Job job2 = new Job();
            job2.setTitle("Kỹ sư Quản lý Chất lượng QA/QC Thủy sản");
            job2.setSalary("12 - 16 Triệu");
            job2.setLocation("An Giang");
            job2.setExperience("1 năm");
            job2.setWorkingType("Toàn thời gian");
            job2.setQuantity(3);
            job2.setDescription("Kiểm soát chất lượng quy trình chế biến xuất khẩu tại nhà máy An Giang theo chuẩn HACCP/BRC.");
            job2.setBenefit("Bao cơm ca, có xe đưa rước từ Long Xuyên, thưởng KPI hàng quý.");
            job2.setDeadline(deadline);
            job2.setCompany(comp2);
            jobRepository.save(job2);

            Job job3 = new Job();
            job3.setTitle("Chuyên viên Kinh doanh Nông nghiệp Kỹ thuật cao");
            job3.setSalary("10 - 20 Triệu");
            job3.setLocation("Đồng Tháp");
            job3.setExperience("Không yêu cầu");
            job3.setWorkingType("Toàn thời gian");
            job3.setQuantity(10);
            job3.setDescription("Phát triển thị trường, tư vấn kỹ thuật giống cây trồng cho nhà nông tại khu vực Đồng Tháp, Tiền Giang.");
            job3.setBenefit("Hoa hồng doanh số hấp dẫn, hỗ trợ xăng xe điện thoại.");
            job3.setDeadline(deadline);
            job3.setCompany(comp3);
            jobRepository.save(job3);

            Job job4 = new Job();
            job4.setTitle("Thực tập sinh Web Frontend (VueJS/React)");
            job4.setSalary("4 - 6 Triệu");
            job4.setLocation("Cần Thơ");
            job4.setExperience("Chưa có kinh nghiệm");
            job4.setWorkingType("Thực tập");
            job4.setQuantity(8);
            job4.setDescription("Thực tập dự án thực tế dưới sự hướng dẫn của Senior Mentor tại văn phòng Ninh Kiều, Cần Thơ.");
            job4.setBenefit("Hỗ trợ trợ cấp thực tập, cơ hội trở thành nhân viên chính thức.");
            job4.setDeadline(deadline);
            job4.setCompany(comp1);
            jobRepository.save(job4);

            System.out.println(">>> Đã khởi tạo dữ liệu mẫu thành công!");
        }
    }
}
