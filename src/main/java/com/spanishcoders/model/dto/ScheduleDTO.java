package com.spanishcoders.model.dto;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Appointment;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Role;
import org.springframework.security.core.Authentication;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by agustin on 27/09/16.
 */
public class ScheduleDTO implements Comparable<ScheduleDTO> {

    private final Integer blockId;

    private final String start;

    private final String length;

    private final String workingDay;

    private final Integer hairdresserId;

    private final Integer appointmentId;

    private final String client;

    private final Set<Integer> worksIds;

    private final String notes;

    public ScheduleDTO() {
        blockId = 0;
        start = "";
        length = "";
        workingDay = "";
        hairdresserId = 0;
        appointmentId = 0;
        client = "";
        worksIds = Sets.newHashSet();
        notes = "";
    }

    public ScheduleDTO(Authentication authentication, Block block) {
        blockId = block.getId();
        start = block.getStart().toString();
        length = block.getLength().toString();
        workingDay = block.getWorkingDay() != null ? block.getWorkingDay().getDate().toString() : "";
        hairdresserId = block.getWorkingDay().getAgenda().getHairdresser().getId();
        appointmentId = block.getAppointment() != null ? block.getAppointment().getId() : 0;
        if (isWorker(authentication) || isProprietary(authentication, block.getAppointment())) {
            client = block.getAppointment() != null ? block.getAppointment().getUser().getName() : "";
            worksIds = Sets.newHashSet();
            if (block.getAppointment() != null) {
                worksIds.addAll(block.getAppointment().getWorks().stream().map(work -> work.getId()).collect(Collectors.toSet()));
            }
            notes = block.getAppointment() != null ? block.getAppointment().getNotes() : "";
        } else {
            client = "";
            worksIds = Sets.newHashSet();
            notes = "";
        }
    }

    private boolean isProprietary(Authentication authentication, Appointment appointment) {
        boolean isProprietary = false;
        if (authentication != null && appointment != null) {
            isProprietary = authentication.getName().equals(appointment.getUser().getName());
        }
        return isProprietary;
    }

    private boolean isWorker(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.equals(Role.WORKER.getGrantedAuthority()));
    }

    public Integer getBlockId() {
        return blockId;
    }

    public String getStart() {
        return start;
    }

    public String getLength() {
        return length;
    }

    public String getWorkingDay() {
        return workingDay;
    }

    public Integer getHairdresserId() {
        return hairdresserId;
    }

    public Integer getAppointmentId() {
        return appointmentId;
    }

    public String getClient() {
        return client;
    }

    public Set<Integer> getWorksIds() {
        return worksIds;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScheduleDTO that = (ScheduleDTO) o;

        return blockId != null ? blockId.equals(that.blockId) : that.blockId == null;

    }

    @Override
    public int hashCode() {
        return blockId != null ? blockId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ScheduleDTO{" +
                "blockId=" + blockId +
                ", start='" + start + '\'' +
                ", length='" + length + '\'' +
                ", workingDay='" + workingDay + '\'' +
                ", hairdresserId=" + hairdresserId +
                ", client='" + client + '\'' +
                ", worksIds=" + worksIds +
                ", notes='" + notes + '\'' +
                '}';
    }

    @Override
    public int compareTo(ScheduleDTO o) {
        if (start.equals(o.getStart())) {
            return workingDay.compareTo(o.getWorkingDay());
        } else {
            return start.compareTo(o.getStart());
        }
    }
}
