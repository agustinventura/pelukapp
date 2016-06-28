package com.spanishcoders.services;

import com.google.common.collect.Sets;
import com.spanishcoders.model.Agenda;
import com.spanishcoders.model.Block;
import com.spanishcoders.model.Work;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by agustin on 28/06/16.
 */
@Service
public class AgendaService {

    public Set<Block> getFirstTenAvailableBlocks(Agenda agenda, Set<Work> works) {
        Set<Block> availableBlocks = Sets.newHashSet();

        return availableBlocks;
    }
}
