import { Pipe, PipeTransform } from "@angular/core";

@Pipe({
    name: "extractTitle"
})
export class TitlePipe implements PipeTransform {

    transform(note, limit): string {
        limit = parseInt(limit);

        note = note.Item;
        console.log(note);
        if (note.title) {
            if(note.title.length == 0) {
                return this.getTitleFromString(note.content, limit);    
            } else {
                return this.getTitleFromString(note.title, limit);
            }
        } else {
            return this.getTitleFromString(note.content, limit);
        }
    }

    getTitleFromString(str, limit) {
        if (str.length > limit) {
            return str.slice(0, limit) + '...';
        } else {
            return str;
        }
    }
}
