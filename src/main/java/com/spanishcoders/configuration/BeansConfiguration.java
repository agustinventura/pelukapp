package com.spanishcoders.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.spanishcoders.agenda.AgendaMapper;
import com.spanishcoders.agenda.AgendaMapperImpl;
import com.spanishcoders.appointment.AppointmentMapper;
import com.spanishcoders.appointment.AppointmentMapperImpl;
import com.spanishcoders.error.ErrorMapper;
import com.spanishcoders.error.ErrorMapperImpl;
import com.spanishcoders.user.UserMapper;
import com.spanishcoders.user.UserMapperImpl;
import com.spanishcoders.user.client.ClientMapper;
import com.spanishcoders.user.client.ClientMapperImpl;
import com.spanishcoders.user.hairdresser.HairdresserMapper;
import com.spanishcoders.user.hairdresser.HairdresserMapperImpl;
import com.spanishcoders.work.WorkMapper;
import com.spanishcoders.work.WorkMapperImpl;
import com.spanishcoders.workingday.block.BlockMapper;
import com.spanishcoders.workingday.block.BlockMapperImpl;
import com.spanishcoders.workingday.schedule.ScheduleMapper;
import com.spanishcoders.workingday.schedule.ScheduleMapperImpl;

@Configuration
public class BeansConfiguration {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public ClientMapper clientMapper() {
		return new ClientMapperImpl();
	}

	@Bean
	public ErrorMapper errorMapper() {
		return new ErrorMapperImpl();
	}

	@Bean
	public HairdresserMapper hairdresserMapper() {
		return new HairdresserMapperImpl();
	}

	@Bean
	public BlockMapper blockMapper() {
		return new BlockMapperImpl();
	}

	@Bean
	public AppointmentMapper appointmentMapper() {
		return new AppointmentMapperImpl();
	}

	@Bean
	public AgendaMapper agendaMapper() {
		return new AgendaMapperImpl();
	}

	@Bean
	public ScheduleMapper scheduleMapper() {
		return new ScheduleMapperImpl();
	}

	@Bean
	public UserMapper userMapper() {
		return new UserMapperImpl();
	}

	@Bean
	public WorkMapper workMapper() {
		return new WorkMapperImpl();
	}
}
