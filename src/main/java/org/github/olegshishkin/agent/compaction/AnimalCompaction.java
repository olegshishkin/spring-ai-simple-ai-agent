package org.github.olegshishkin.agent.compaction;

import org.springframework.context.annotation.Description;

public record AnimalCompaction(@Description("Идентификатор записи") Integer id,
                               @Description("Континент обитания животного") String continent,
                               @Description("Наименование животного") String name,
                               @Description("Цвет животного") String color,
                               @Description("Коэффициент компактности") String compaction) {

}
